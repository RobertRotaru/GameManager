import WebSocket from "ws";
import jwt from "jsonwebtoken";
import { jwtConfig } from "./utils.js";

let wss;

export const initWss = (value) => {
    wss = value;
    wss.on('connection', (ws) => {
        ws.on('message', (msg) => {
            const { type, payload: { token } } = JSON.parse(msg);
            if (type !== 'authorization') {
                ws.close();
                return;
            }
            try {
                // Validate and decode the token
                const user = jwt.verify(token, jwtConfig.secret);
                // Ensure user object is valid and contains _id
                if (user && user._id) {
                    ws.user = user;
                } else {
                    ws.close(); // Close if user is invalid
                }
            } catch (err) {
                ws.close(); // Close if token verification fails
            }
        });
    });
};

export const broadcast = (userId, data) => {
    if (!wss) {
        console.warn("WebSocket server not initialized.");
        return;
    }
    wss.clients.forEach(client => {
        if (client.readyState === WebSocket.OPEN) {
            if (client.user && client.user._id === userId) {
                try {
                    client.send(JSON.stringify(data));
                    console.log("Success!");
                } catch (error) {
                    console.error(`Failed to send message to ${client.user.username}:`, error);
                }
            }
        }
    });
};