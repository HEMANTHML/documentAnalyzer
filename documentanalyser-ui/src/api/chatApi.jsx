import api from './axiosInstance';

// Ask a question about a specific document
export const askQuestion = (docId, question) =>
    api.post(`/chat/${docId}`, { question });