import React, {useState} from 'react';
import {checkAuthentication, getHeaders} from '../services/AuthHeader';
import axios from 'axios';

const ChangePassword = () => {
    checkAuthentication();
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        const requestParam = {
            oldPassword,
            newPassword,
        };

        axios.put(`${process.env.REACT_APP_SPRING_HOST}/users/change-password`, {}, {
            headers: {
                'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params:  {
                username: localStorage.getItem('username'),
                oldPassword: requestParam.oldPassword,
                newPassword: requestParam.newPassword
            }
                })
            .then((response) => {
                setSuccess('Password changed successfully');
                setError('');
            })
            .catch((error) => {
                setError(error.response.data.message);
                setSuccess('');
            });
    };

    return (
        <form onSubmit={handleSubmit}>
            <label>
                Old password:
                <input type="password" name="oldPassword" value={oldPassword} onChange={(event) => setOldPassword(event.target.value)} />
            </label>
            <br />
            <label>
                New password:
                <input type="password" name="newPassword" value={newPassword} onChange={(event) => setNewPassword(event.target.value)} />
            </label>
            <br />
            <button type="submit">Change password</button>
            {error && <div style={{color: 'red'}}>{error}</div>}
            {success && <div style={{color: 'green'}}>{success}</div>}
        </form>
    );
};

export default ChangePassword;
