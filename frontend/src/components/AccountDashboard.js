import React, {useEffect, useState} from "react";
import axios from "axios";
import {Link} from "react-router-dom";
import {checkAuthentication} from "../services/AuthHeader";

const ChangePassword = ({setRenderChangePassword, renderChangePassword}) => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);
        if (newPassword !== confirmPassword) {
            setError("New password and confirm password do not match");
            return;
        }
        axios
            .put(`${process.env.REACT_APP_SPRING_HOST}/users/change-password`, {}, {
                params: {
                    oldPassword: oldPassword,
                    newPassword: newPassword,
                    username: localStorage.getItem("username"),
                },
                headers: {
                    Authorization: 'Basic ' + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password")),
                }
            })
            .then((response) => {
                setSuccess(response.data);
                localStorage.setItem("password", newPassword);
                setOldPassword("");
                setNewPassword("");
                setConfirmPassword("");

            })
            .catch((error) => {
                setError(error.response.data.message);
            });
    };

    if (renderChangePassword) {
        return (
            <div>
                <br/>
                <h2>Change Password</h2>
                <div style={{marginRight: "60px", padding: "10px"}}>
                <form onSubmit={handleSubmit} style={{display: "flex", flexDirection: "column"}}>
                    <label htmlFor="old-password" style={{textAlign: "left"}}>Old Password:</label>
                    <input
                        type="password"
                        id="old-password"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        required
                        style={{marginBottom: "10px"}}
                    />
                    <label htmlFor="new-password" style={{textAlign: "left"}}>New Password:</label>
                    <input
                        type="password"
                        id="new-password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        style={{marginBottom: "10px"}}
                    />
                    <label htmlFor="confirm-password" style={{textAlign: "left"}}>Confirm New Password:</label>
                    <input
                        type="password"
                        id="confirm-password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                        style={{marginBottom: "10px"}}
                    />
                    <button type="submit" style={{marginLeft: "auto"}}>Change Password</button>
                </form>
                {error && <div style={{color: "red"}}>{error}</div>}
                {success && <div style={{color: "green"}}>{success}</div>}
                <br/>
                <br/>
            </div>
            </div>
        );

    }
};


const ChangeEmail = ({setUrlApproval, setRenderChangePassword, urlApproval, renderChangePassword,codeSent,setCodeSent}) => {
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [approved, setApproved] = useState(false);

    useEffect(() => {
        // Check if a reset code was provided in the URL
        const params = new URLSearchParams(window.location.search);
        const resetCode = params.get('code');
        if (resetCode) {
            setCode(resetCode);
            setCodeSent(true);
            setUrlApproval(true);
            setRenderChangePassword(false);
            axios.put(`${process.env.REACT_APP_SPRING_HOST}/users/change-email`, {},
                {
                    params: {
                        code: resetCode,
                        username: localStorage.getItem("username")
                    },
                    headers: {
                        'Authorization': 'Basic ' + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }
                })
                .then((response) => {
                    localStorage.setItem("hasEmail", "true");
                    setApproved(true);
                    setSuccess(response.data);
                    setRenderChangePassword(true);
                })
                .catch((error) => {
                    setError(error.response.data.message);
                });
        }

    }, []);

    const handleSendCode = (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);
        axios.put(`${process.env.REACT_APP_SPRING_HOST}/users/send-email-code`, {}, {
            headers: {
                'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: {
                email: email,
                username: localStorage.getItem("username"),
            }

        })
            .then((response) => {
                setSuccess(response.data);
                setRenderChangePassword(false);
                setCodeSent(true);


            })
            .catch((error) => {
                setError(error.response.data.message);
            });
    };

    const handleApproveEmail = (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);
        axios.put(`${process.env.REACT_APP_SPRING_HOST}/users/change-email`, {},
            {
                params: {
                    code: code,
                    username: localStorage.getItem("username")
                },
                headers: {
                    'Authorization': 'Basic ' + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }
            })
            .then((response) => {
                setApproved(true);
                localStorage.setItem("hasEmail", "true");
                setSuccess(response.data);
                setRenderChangePassword(true);

            })
            .catch((error) => {
                setError(error.response.data.message);
            });
    };

    return (
        console.log("codesent:" + codeSent),
            console.log("renderChangePassword:" + renderChangePassword),
        <div>
            <br/>
            <h2>
                {codeSent
                    ? "Verify Email Code"
                    : localStorage.getItem("hasEmail") === "true"
                        ? "Change Email"
                        : "Set Email"}
            </h2>
            <br/>
            {codeSent ? (
                urlApproval ? (
                    <div></div>
                ) : (
                    approved ? (
                        <div>
                        </div>
                    ) : (
                            <div style={{marginLeft: "60px", padding: "10px"}}>
                        <form onSubmit={handleApproveEmail} style={{display: "flex", flexDirection: "column"}}>
                            <div style={{display: "flex", flexDirection: "column"}}>
                                <label htmlFor="code"style={{textAlign: "left"}}>Code:</label>
                                <input
                                    type="text"
                                    id="code"
                                    value={code}
                                    onChange={(e) => setCode(e.target.value)}
                                    required
                                />
                            </div>
                            <br />
                            <button type="submit"style={{marginLeft: "auto"}}>Verify Email</button>
                            <br />
                            <Link onClick={() => setCodeSent(false)}>Didn't Receive Email?</Link>
                        </form>
                        </div>
                    )
                )
            ) : (
                <div style={{marginLeft: "60px", padding: "10px"}}>
                <form onSubmit={handleSendCode} style={{display: "flex", flexDirection: "column"}}>
                    <div style={{display: "flex", flexDirection: "column"}}>
                        <label htmlFor="email"style={{textAlign: "left"}}>Email:</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <br/>
                    <button type="submit" style={{alignSelf: "flex-end"}}>Send Verification Code</button>
                    <br/><Link onClick={() => setCodeSent(true)}>Already have a code?</Link>
                </form>
                </div>
            )}
            <br/>
            {error && <div style={{color: "red"}}>{error}</div>}
            {success && <div style={{color: "green"}}>{success}</div>}
            <br/>
            <br/>
        </div>
    );

};

const EmailComponent = ({setUrlApproval, setRenderChangePassword, urlApproval, renderChangePassword,codeSent, setCodeSent}) => {

    if (localStorage.getItem("hasEmail") === "true") {
        return <ChangeEmail
            codeSent={codeSent}
            setCodeSent={setCodeSent}
            setUrlApproval={setUrlApproval}
            setRenderChangePassword={setRenderChangePassword}
            urlApproval={urlApproval}
            renderChangePassword={renderChangePassword}
        />;
    } else {
        setRenderChangePassword(false);
        return (
            <div>
                <br/>
                <h4>
                    Using Laborant app without email is restricted, please enter an email
                    in order to use.
                </h4>
                <ChangeEmail
                    codeSent={codeSent}
                    setCodeSent={setCodeSent}
                    setUrlApproval={setUrlApproval}
                    setRenderChangePassword={setRenderChangePassword}
                    urlApproval={urlApproval}
                    renderChangePassword={renderChangePassword}
                />
            </div>
        );
    }
};

const Dashboard = () => {
    checkAuthentication();
    const [renderChangePassword, setRenderChangePassword] = useState(true);
    const [urlApproval, setUrlApproval] = useState(false);
    const [codeSent, setCodeSent] = useState(false);
    if (renderChangePassword) {
        return (
            <div style={{display: "flex", flexDirection: "row"}}>
                <div style={{flex: 1}}>
                    <ChangePassword
                        setRenderChangePassword={setRenderChangePassword}
                        setUrlApproval={setUrlApproval}
                        renderChangePassword={renderChangePassword}
                        urlApproval={urlApproval}
                    />

                </div>

                <div style={{flex: 1}}>

                    <EmailComponent
                        codeSent={codeSent}
                        setCodeSent={setCodeSent}
                        setRenderChangePassword={setRenderChangePassword}
                        setUrlApproval={setUrlApproval}
                        renderChangePassword={renderChangePassword}
                        urlApproval={urlApproval}/>
                </div>
            </div>
        );
    } else {
        return (
            <div>
                <EmailComponent
                    codeSent={codeSent}
                    setCodeSent={setCodeSent}
                    setRenderChangePassword={setRenderChangePassword}
                    setUrlApproval={setUrlApproval}
                    renderChangePassword={renderChangePassword}
                    urlApproval={urlApproval}/>
            </div>
        );
    }
};

export default Dashboard;
