import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllDocuments, uploadDocument, deleteDocument } from '../api/documentApi';

export default function DocumentsPage() {
    const [documents, setDocuments] = useState([]);
    const [uploading, setUploading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => { fetchDocs(); }, []);

    const fetchDocs = async () => {
        const res = await getAllDocuments();
        setDocuments(res.data);
    };

    const handleUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setUploading(true);
        try {
            await uploadDocument(file);
            await fetchDocs();
        } finally {
            setUploading(false);
        }
    };

    const handleDelete = async (id) => {
        await deleteDocument(id);
        setDocuments(prev => prev.filter(d => d.id !== id));
    };

    return (
        <div className="p-8 max-w-4xl mx-auto">
            <h1 className="text-2xl font-bold mb-6">My Documents</h1>

            {/* Upload button */}
            <label className="cursor-pointer bg-blue-600 text-white px-4 py-2 rounded">
                {uploading ? 'Uploading...' : 'Upload PDF'}
                <input type="file" accept=".pdf" className="hidden" onChange={handleUpload} />
            </label>

            {/* Document list */}
            <div className="mt-8 space-y-4">
                {documents.map(doc => (
                    <div key={doc.id}
                         className="flex items-center justify-between p-4 border rounded-lg">
                        <div>
                            <p className="font-medium">{doc.originalName}</p>
                            <p className="text-sm text-gray-500">
                                {doc.pageCount} pages • {(doc.fileSize/1024).toFixed(1)} KB • {doc.status}
                            </p>
                        </div>
                        <div className="flex gap-2">
                            <button
                                onClick={() => navigate(`/chat/${doc.id}`)}
                                className="bg-green-600 text-white px-3 py-1 rounded text-sm">
                                Ask AI
                            </button>
                            <button
                                onClick={() => handleDelete(doc.id)}
                                className="bg-red-500 text-white px-3 py-1 rounded text-sm">
                                Delete
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}