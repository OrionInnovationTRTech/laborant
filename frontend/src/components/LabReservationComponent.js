import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import axios from "axios";
import {useState} from "react";
import moment from 'moment';

const LabReservationForm = () => {
    const [selectedDate, setSelectedDate] = useState(null);
    const [formValues, setFormValues] = useState({});
    const [message, setMessage] = useState('');
    const labName = window.location.pathname.split('/')[2];

    const handleSubmit = (event) => {
        event.preventDefault();
        const reservedUntil = moment(selectedDate).format("YYYY-MM-DD HH:mm:ss");
        const valuesWithReservedUntil = { ...formValues, reservedUntil };
        const date = moment(selectedDate).format("YYYY-MM-DD");
        const hour = moment(selectedDate).format("HH:mm:ss");
        console.log(process.env.HOST);
        axios.put(`${process.env.REACT_APP_SPRING_HOST}/v1/reserve-lab/`, {}, {
            headers: {
                'Authorization' : 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: {
                labName: labName,
                date: reservedUntil,
            }

        })
            .then((response) => {
                setMessage(`Lab reserved successfully until date: ${date} hour: ${hour}`);
            })
            .catch((error) => {
                setMessage(error.response.data.message);
            });
    };

    return (
        <form onSubmit={handleSubmit}>
            <DatePicker
                showTimeSelect
                selected={selectedDate}
                onChange={setSelectedDate}
            />
            <button type="submit">Reserve Lab</button>
            {message && <div>{message}</div>}
        </form>
    );
};

export default LabReservationForm;
