import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom';
import DocumentsPage from './pages/DocumentPage';
import ChatPage from './pages/ChatPage';

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/documents"/>}/>
                <Route path="/documents" element={<DocumentsPage/>}/>
                <Route path="/chat/:docId" element={<ChatPage/>}/>
            </Routes>
        </BrowserRouter>
    );
}