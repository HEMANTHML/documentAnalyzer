import {useState} from 'react';
import {useParams} from 'react-router-dom';
import {askQuestion} from '../api/chatApi';

export default function ChatPage() {
    const {docId} = useParams();
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSend = async () => {
        if (!input.trim()) return;
        const question = input;
        setInput('');

        // Add user message
        setMessages(prev => [...prev, {role: 'user', text: question}]);
        setLoading(true);

        try {
            const res = await askQuestion(docId, question);
            const {answer, sources} = res.data;
            setMessages(prev => [...prev, {role: 'ai', text: answer, sources}]);
        } catch (err) {
            setMessages(prev => [...prev, {role: 'ai', text: 'Error: could not get answer.'}]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col h-screen max-w-3xl mx-auto p-4">
            <h1 className="text-xl font-bold mb-4">Document Chat</h1>

            {/* Messages */}
            <div className="flex-1 overflow-y-auto space-y-4 mb-4">
                {messages.map((msg, i) => (
                    <div key={i} className={`p-3 rounded-lg ${msg.role === 'user'
                        ? 'bg-blue-100 ml-12' : 'bg-gray-100 mr-12'}`}>
                        <p className="text-sm font-bold mb-1">
                            {msg.role === 'user' ? 'You' : 'DocMind AI'}
                        </p>
                        <p>{msg.text}</p>
                        {/* Source snippets */}
                        {msg.sources && msg.sources.length > 0 && (
                            <details className="mt-2 text-xs text-gray-500">
                                <summary>View sources ({msg.sources.length})</summary>
                                {msg.sources.map((s, si) => (
                                    <p key={si} className="mt-1 border-l-2 border-gray-300 pl-2">{s}</p>
                                ))}
                            </details>
                        )}
                    </div>
                ))}
                {loading && <p className="text-gray-400 italic">DocMind is thinking...</p>}
            </div>

            {/* Input */}
            <div className="flex gap-2">
                <input
                    className="flex-1 border rounded-lg px-4 py-2 focus:outline-none focus:ring-2"
                    placeholder="Ask a question about this document..."
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && handleSend()}
                />
                <button
                    onClick={handleSend}
                    disabled={loading}
                    className="bg-blue-600 text-white px-4 py-2 rounded-lg disabled:opacity-50">
                    Send
                </button>
            </div>
        </div>
    );
}