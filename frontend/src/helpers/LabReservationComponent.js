import { useState } from 'react';
import axios from 'axios';
import moment from 'moment';
import Modal from 'react-bootstrap/Modal';
import { Button } from 'react-bootstrap';

const LabReservationForm = ({ labName, showModal, handleCloseModal }) => {
    const [selectedDate, setSelectedDate] = useState(moment());
    const [message, setMessage] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        const reservedUntil = selectedDate.format('YYYY-MM-DD HH:mm:ss');
        const date = selectedDate.format('YYYY-MM-DD');
        const hour = selectedDate.format('HH:mm:ss');
        console.log(process.env.HOST);
        axios
            .put(
                `${process.env.REACT_APP_SPRING_HOST}/v1/reserve-lab/`,
                {},
                {
                    headers: {
                        Authorization:
                            'Basic ' +
                            btoa(
                                localStorage.getItem('username') +
                                ':' +
                                localStorage.getItem('password')
                            ),
                    },
                    params: {
                        labName: labName,
                        date: reservedUntil,
                    },
                }
            )
            .then((response) => {
                setMessage(
                    `Lab reserved successfully until date: ${date} hour: ${hour}`
                );
                window.location.replace(`/`);
            })
            .catch((error) => {
                setMessage(error.response.data.message);
            });
    };

    const handleDateChange = (date) => {
        setSelectedDate(date);
    };

    return (
        <Modal show={showModal} onHide={handleCloseModal} size="lg">
            <Modal.Header closeButton>
                <Modal.Title>Reserve - {labName}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="d-flex align-items-center mb-3">
                    <label className="me-2 mb-0">Date and Time:</label>
                    <input
                        type="datetime-local"
                        value={selectedDate.format('YYYY-MM-DDTHH:mm')}
                        onChange={(e) =>
                            handleDateChange(moment(e.target.value, 'YYYY-MM-DDTHH:mm'))
                        }
                    />
                </div>
                {message && <div>{message}</div>}
                <br />
                <button type="submit" className="btn btn-primary" onClick={handleSubmit}>
                    Reserve
                </button>
                <Button variant="secondary" onClick={handleCloseModal}>
                    Close
                </Button>{' '}
            </Modal.Body>
        </Modal>
    );
};

export default LabReservationForm;
