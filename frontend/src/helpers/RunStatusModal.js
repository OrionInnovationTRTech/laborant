import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { getHeaders } from '../services/AuthHeader';
import { Modal, Button } from 'react-bootstrap';
import { Table } from './StatusTable';

const RunStatusModal = ({ labName, showModal, handleCloseModal }) => {
    const [response, setResponse] = useState('');

    useEffect(() => {
        if (showModal) {
            runCommand();
        }
    }, [showModal]);

    const runCommand = () => {
        setResponse('Running on progress...');
        axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/labs/status/${labName}`, getHeaders())
            .then((response) => {
                const data = response.data.split('\n').map(row => {
                    const elements = row.split(' ').filter(element => element !== '');
                    return elements;
                }).filter(row => row.length > 0);
                console.log(data);

                if (data[0] !== undefined && data[0] !== null) {
                    setResponse(<Table data={data} />);
                } else {
                    setResponse("Can't retrieve status")
                }
            })
            .catch((error) => {
                setResponse(`Error: ${error.response.data.message}`);
            });
    };

    return (
        <Modal show={showModal} onHide={handleCloseModal} size="lg">
            <Modal.Header closeButton>
                <Modal.Title>Run Status - {labName}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div>{response}</div>
                <br/>
                <br/>
                <br/>
                <br/>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
            </Modal.Footer>
        </Modal>
    );
};

export default RunStatusModal;
