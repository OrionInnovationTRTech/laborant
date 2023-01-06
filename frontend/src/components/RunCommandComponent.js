import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { getHeaders } from '../services/AuthHeader';
import {Table} from './StatusTable';

const RunCommandComponent = () => {
  const labName = window.location.pathname.split('/')[2];
  const [response, setResponse] = useState('');

  useEffect(() => {

    runCommand();
}, []);

  const runCommand = () => {
    setResponse('Running on progress...');
    axios.get(`http://localhost:8080/v1/labs/status/${labName}`,getHeaders())
    .then((response) => {
         const data = response.data.split('\n').map(row => {
            const elements = row.split(' ').filter(element => element !== '');
            return elements;
        }).filter(row => row.length > 0);
        console.log(data);

        if(data[0] !== undefined && data[0] !== null){
            setResponse(<Table data={data}/>);
        }
        else {
            setResponse("Can't retrieve status")
        }
    })
    .catch((error) => {
      setResponse(`Error: ${error.response.data.message}`);
    });
  };

  return (
    <div>
      <div>{response}</div>
    </div>
  );
};

export default RunCommandComponent;