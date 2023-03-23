import React ,{useState} from "react";
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
import axios from "axios";
import {Link} from "react-router-dom";
const base = process.env.REACT_APP_BASE_PATH || '';


const AddUserComponent = () => {
    checkAuthentication();
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [email, setEmail] = useState('');
    const [role, setRole] = useState('USER');

    const saveUser = (e) => {
        e.preventDefault();
        let user = {email:email,user_role:role};

        axios.post(`${process.env.REACT_APP_SPRING_HOST}/users/add-user-with-email`,user,getHeaders())
            .then((response) => {
                console.log(response.status);
                if (response.status === 200) {
                    setMessage('User Added Successfully. Redirecting...');
                    setTimeout(() => {
                        window.location.replace(base+'/users');
                    }, 1500);
                } else if (response.status === 400) {
                    setError(`Failed to add user. ${response.data.message}`);
                } else {
                    setError(`Failed to add user. HTTP status code: ${response.status}`);
                }
            }).catch((error) => {
            setError(`Failed to add user. Error: ${error.response.data.message}`);
        });

        console.log('user => ' + JSON.stringify(user));
    }

    const handleRoleChange = (e) => {
        setRole(e.target.value);
    }

    return(
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                    <br />
                    <h3 className="text-center">
                        Add User<Link to="/bulk-add-user" className={"btn btn-primary float-right"} style={{ marginLeft: '20px' }}>Bulk Add</Link>
                    </h3>
                    <div className="card-body">
                        <form>
                            <div className="form-group">
                                <label className="form-label">Email:</label>
                                <input
                                    type="text"
                                    placeholder="Enter Email"
                                    name="username"
                                    className="form-control"
                                    value={email}
                                    onChange={e => setEmail(e.target.value)}
                                />
                            </div>

                            <div className="form-group" style={{ marginTop: '20px' }}>
                                <label className="form-label" style={{ marginBottom: '10px', display: 'block' }}>Role:</label>
                                <div className="form-check form-check-inline" style={{ marginBottom: '10px' }}>
                                    <input
                                        type="radio"
                                        name="role"
                                        value="USER"
                                        checked={role === "USER"}
                                        onChange={handleRoleChange}
                                        className="form-check-input"
                                    />
                                    <label className="form-check-label" style={{ marginLeft: '5px' }}>
                                        User
                                    </label>
                                </div>

                                <div className="form-check form-check-inline" style={{ marginBottom: '20px' }}>
                                    <input
                                        type="radio"
                                        name="role"
                                        value="ADMIN"
                                        checked={role === "ADMIN"}
                                        onChange={handleRoleChange}
                                        className="form-check-input"
                                    />
                                    <label className="form-check-label" style={{ marginLeft: '5px' }}>
                                        Admin
                                    </label>
                                </div>

                                <div>
                                    {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
                                    {message && <div style={{ color: 'green', marginBottom: '10px' }}>{message}</div>}
                                </div>

                                <button className="btn btn-success" onClick={(e) => saveUser(e)}>Save</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

    )
}

export default AddUserComponent;

