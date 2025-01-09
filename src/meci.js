import Router from 'koa-router';
import dataStore from 'nedb-promise';
import { broadcast } from './wss.js';

export class MeciStore {
    constructor({filename, autoload}) {
        this.store = dataStore({filename, autoload});
    }

    async find(props, page, pageSize, hasStarted) {
        if (page < 0 || pageSize < 1) {
            throw new Error('Invalid page or pageSize value');
        }
        const skip = page * pageSize;
        const limit = pageSize;

        const allRecords = await this.store.find(props);
        const filteredRecords = allRecords.filter((record) => record.hasStarted === hasStarted);
        console.log(filteredRecords.length);
        const paginatedRecords = filteredRecords.slice(skip, skip + limit);

        console.log(paginatedRecords);

        return {
            records: paginatedRecords,
            pageInfo: {
                currentPage: page,
                pageSize: pageSize,
                totalRecords: allRecords.length,
                totalPages: Math.ceil(allRecords.length / pageSize)
            }
        };
    }

    async search(props, page, pageSize, hasStarted, searchString) {
        if (page < 0 || pageSize < 1) {
            throw new Error('Invalid page or pageSize value');
        }
        const skip = page * pageSize;
        const limit = pageSize;

        const allRecords = await this.store.find(props);
        const filteredRecords = allRecords.filter((record) => record.hasStarted === hasStarted);
        const foundRecords = filteredRecords.filter((record) => record.name.startsWith(searchString));
        const paginatedRecords = foundRecords.slice(skip, skip + limit);

        console.log(paginatedRecords);

        return {
            records: paginatedRecords,
            pageInfo: {
                currentPage: page,
                pageSize: pageSize,
                totalRecords: allRecords.length,
                totalPages: Math.ceil(allRecords.length / pageSize)
            }
        };
    }

    async findOne(props) {
        return this.store.findOne(props);
    }

    async insert(meci) {
        if(!meci.name) {
            throw new Error('Missing name property');
        }
        if(!meci.pretBilet) {
            throw new Error('pretBilet cannot be 0');
        }
        if(!meci.startDate) {
            throw new Error('Missing startDate property');
        }
        return this.store.insert(meci);
    };

    async update(props, meci) {
        return this.store.update(props, meci);
    };

    async delete(props) {
        return this.store.delete(props);
    }
}

const meciStore = new MeciStore({filename: './db/meci.json', autoload: true});

export const meciRouter = new Router();
export const allMeciRouter = new Router();

allMeciRouter.get('/', async (ctx) => {
    const userId = ctx.state.user._id;
    const pageSize = ctx.request.query.pageSize;
    const page = ctx.request.query.page;
    const hasStarted = ctx.request.query.hasStarted === "true";
    console.log(userId);
    ctx.response.body = (await meciStore.find({userId}, page, pageSize, hasStarted)).records;
    ctx.response.status = 200;
});

allMeciRouter.get('/search', async (ctx) => {
    const userId = ctx.state.user._id;
    const pageSize = ctx.request.query.pageSize;
    const page = ctx.request.query.page;
    const hasStarted = ctx.request.query.hasStarted === "true";
    const searchQuery = ctx.request.query.searchString;
    console.log(userId);
    ctx.response.body = await meciStore.search({userId}, page, pageSize, hasStarted, searchQuery);
    ctx.response.status = 200;
})

meciRouter.get('/:id', async (ctx) => {
    const userId = ctx.state.user._id;
    const item = await meciStore.findOne({_id: ctx.params.id});
    const response = ctx.response;
    if(item) {
        if(item.userId === userId) {
            ctx.response.body = item;
            ctx.response.status = 200;
        } else {
            ctx.response.status = 403;
        }
    } else {
        ctx.response.status = 404;
    }
});

const createItem = async (ctx, item, response) => {
    try{
        const userId = ctx.state.user._id;
        const initItem = {...item};
        item.userId = userId;
        response.body = await meciStore.insert(item);
        initItem._id = response.body._id;
        response.status = 201;
        console.log(initItem);
        broadcast(userId, {type: 'created', payload: initItem});
    } catch(err) {
        response.body = {message: err.message};
        response.status = 400;
    }
};

meciRouter.post('/', async (ctx) => await createItem(ctx, ctx.request.body, ctx.response));

meciRouter.put('/:id', async (ctx) => {
    const item = ctx.request.body;
    const id = ctx.params.id;
    const itemId = item._id;
    const response = ctx.response;
    if(itemId && itemId !== id) {
        response.body = {message: `Param id and body id should be the same`};
        response.status = 400;
        return;
    }
    if(!itemId) {
        console.log(itemId)
        await createItem(ctx, item, response);
    } else {
        const userId = ctx.state.user._id;
        item.userId = userId;
        console.log(userId);
        const updatedCount = await meciStore.update({_id: id}, item);
        if(updatedCount === 1) {
            response.body = item;
            response.status = 200;
            broadcast(userId, {type: 'updated', payload: item});
        }
        else {
            response.body = {message: "Resource no longer exists"};
            response.status = 405;
        }
    }
});

meciRouter.delete('/:id', async (ctx) => {
    const userId = ctx.state.user._id;
    const item = await meciStore.findOne({_id: ctx.params.id});
    if(item && userId !== item.userId) {
        ctx.response.status = 403; //forbidden;
    }
    else {
        await meciStore.delete({_id: ctx.params.id});
        ctx.response.status = 204;
    }
});