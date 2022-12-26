import React, { useState } from 'react';
import axios from 'axios';
import Papa from 'papaparse';
import { getHeaders } from '../services/AuthHeader';

const BulkAddLabs = () => {
  const [csvData, setCsvData] = useState('');
  const [response, setResponse] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    const labs = Papa.parse(csvData, {
      header: true,
    }).data;
    console.log(labs);
    axios.post('http://localhost:8080/v1/labs/bulk-add', labs,{
      headers: {
        'Authorization' : 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password'))
      }
    })
      .then((res) => {
        setResponse(res.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 400) {
          setResponse(`Failed to add lab. Error: ${error.response.data.message}`);
        } else {
          setResponse(error.response.data);
        }
      });
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <label htmlFor="csvData">Enter Labs as CSV:</label>
        <br />
        <textarea cols={60} rows={10} 
          id="csvData"
          value={csvData}
          onChange={(e) => setCsvData(e.target.value)}
          
        />
        <br />
        <button type="submit">Add Labs</button>
      </form>
      {response && (
        <div>
          {Array.isArray(response) ? (
            <ul>
              {response.map((r) => (
                <li key={r.labName}>
                  {r.labName}: {r.response}
                </li>
              ))}
            </ul>
          ) : (
            <p>{response}</p>
          )}
        </div>
      )}
    </div>
  );
};

export default BulkAddLabs;
