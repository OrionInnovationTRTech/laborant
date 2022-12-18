import React, { useState } from 'react';
import axios from 'axios';
import { getHeaders } from '../services/AuthHeader';

const RunCommandComponent = () => {
    const labName = window.location.pathname.split('/')[2];
    const [command, setCommand] = useState('');
    const [response, setResponse] = useState('');

  const handleCommandChange = (event) => {
    setCommand(event.target.value);
  };

  const runCommand = () => {
    axios.get(`http://localhost:8080/v1/labs/runCommand/${labName}`,{
        headers: {
            'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
          },
           params: {
        command: command
      }
    })
    .then((response) => {
      setResponse(`Response: ${response.data}`);
    })
    .catch((error) => {
      console.error(error);
    });
  };

  return (
    <div>
      <input type="text" value={command} onChange={handleCommandChange} />
      <button className ="btn btn-success" style={{marginLeft: '50px'}} onClick={runCommand} >Run Command</button>
      <div>{response}</div>
    </div>
  );
};

export default RunCommandComponent;
