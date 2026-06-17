import api from './axiosInstance';

// Upload a PDF file
export const uploadDocument = (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/documents/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
};

// Get all documents
export const getAllDocuments = () => api.get('/documents');

// Delete a document
export const deleteDocument = (id) => api.delete(`/documents/${id}`);