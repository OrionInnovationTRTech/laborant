import React, { useState } from 'react';
import axios from 'axios';
import Papa from 'papaparse';
import {getHeaders } from '../services/AuthHeader';

const BulkAddUsers = () => {
    const [csvData, setCsvData] = useState('');
    const [response, setResponse] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        const users = Papa.parse(csvData, {
            header: true,
        }).data;
        console.log(users);
        axios.post(`${process.env.REACT_APP_SPRING_HOST}/users/bulk-add`, users, getHeaders())
            .then((res) => {
                setResponse(res.data);
            })
            .catch((error) => {
                console.error(error);
                if (error.response.status === 400) {
                    setError(`Failed to add users. ${error.response.data.message}`);
                } else {
                    setError(`Failed to add users. HTTP status code: ${error.response.status}`);
                }
            });
    };

    return (
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                    <h3 className="text-center">Bulk Add Users</h3>
                    <div className="card-body">
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="csvData">Enter Users as CSV:</label>
                                <textarea
                                    className="form-control"
                                    id="csvData"
                                    rows="10"
                                    value={csvData}
                                    onChange={(e) => setCsvData(e.target.value)}
                                />
                            </div>
                            <div>
                                {error && <div style={{ color: 'red' }}>{error}</div>}
                                {response && (
                                    <div>
                                        {Array.isArray(response) ? (
                                            <ul>
                                                {response.map((r) => (
                                                    <li key={r.username}>
                                                        {r.username}: {r.response}
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <div style={{ whiteSpace: 'pre-wrap' }}>{response}</div>
                                        )}
                                    </div>
                                )}
                            </div>
                            <button type="submit" className="btn btn-success">
                                Add Users
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BulkAddUsers;
