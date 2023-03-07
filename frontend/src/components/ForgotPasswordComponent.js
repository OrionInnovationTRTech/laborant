import React, {useState, useEffect} from 'react';
import axios from 'axios';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

function PasswordResetComponent() {
    const [email, setEmail] = useState('');
    const [code, setCode] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [resetLinkSent, setResetLinkSent] = useState(false);
    const [codeSubmitted, setCodeSubmitted] = useState(false);
    const [resetSuccess, setResetSuccess] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        // Check if a reset code was provided in the URL
        const params = new URLSearchParams(window.location.search);
        const resetCode = params.get('code');
        if (resetCode) {
            setCode(resetCode);
            setCodeSubmitted(true);
        }
    }, []);

    const handleEmailSubmit = (event) => {
        event.preventDefault();
        axios
            .put(`${process.env.REACT_APP_SPRING_HOST}/pw/forgot-password`, {}, {
                params: {
                    email: email,
                },
            })
            .then(() => {
                setResetLinkSent(true);
            })
            .catch((error) => {
                setError(error.response.data.message);
                window.alert(error.response.data.message);
            });
    };

    const handleCodeSubmit = (event) => {
        event.preventDefault();
        setCodeSubmitted(true);
    };

    const handlePasswordReset = (event) => {
        event.preventDefault();
        axios
            .put(
                `${process.env.REACT_APP_SPRING_HOST}/pw/reset-password`,
                {},
                {
                    params: {
                        code: code,
                        newPassword: newPassword,
                    },
                }
            )
            .then(() => {
                setResetSuccess(true);
            })
            .catch((error) => {
                setError(error.response.data.message);
                window.alert(error.response.data.message);
            });
    };

    if (resetSuccess) {
        return <div style={{ marginTop: '180px' }}>
            <h2>Password reset successful!</h2>
            <p>Your password has been reset successfully. You can now use your new password to log in.</p>
        </div>


    }

    if (codeSubmitted) {
        return (
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
            <form onSubmit={handlePasswordReset}>
                    <h2>Enter your new password</h2>
                    <TextField
                        required
                        fullWidth
                        label="New Password"
                        type="password"
                        value={newPassword}
                        onChange={(event) => setNewPassword(event.target.value)}
                        variant="outlined"
                        margin="normal"
                    />
                    <TextField
                        required
                        fullWidth
                        label="Confirm New Password"
                        type="password"
                        value={confirmPassword}
                        onChange={(event) => setConfirmPassword(event.target.value)}
                        variant="outlined"
                        margin="normal"
                    />
                    <br/>
                    <Button type="submit"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                    >
                        Reset Password
                    </Button>
            </form>
            </Box>
        );
    }

    if (resetLinkSent) {
        return (
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <form onSubmit={handleCodeSubmit}>
                <h2>Enter the code sent to your email</h2>
                    <TextField
                        required
                        fullWidth
                        label="Reset Code"
                        type={"text"}
                        value={code}
                        onChange={(event) => setCode(event.target.value)}
                        variant="outlined"
                        margin="normal"
                    />
                    <Button type="submit"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                            color="primary">
                        Submit
                    </Button>
            </form>
            </Box>
        );
    }

    return (
        <Box
            sx={{
                marginTop: 8,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
            }}
        >
        <form onSubmit={handleEmailSubmit}>
                <h2>Reset Your Password</h2>
                <TextField
                    required
                    fullWidth
                    label="Enter your email address"
                    type="email"
                    variant="outlined"
                    value={email}
                    margin={"normal"}
                    onChange={(event) => setEmail(event.target.value)}
                />
                <br/><br/>
                <Button type="submit"
                        fullWidth
                        variant="contained"
                        sx={{mt: 3, mb: 2}}
                        color="primary">
                    Send Reset Link
                </Button>
                <br/>
                <Typography>
                    <Button onClick={() => setResetLinkSent(true)}>
                        Already have a code?
                    </Button>
                </Typography>
                <br/>
                {error && <Typography color="error">{error}</Typography>}
        </form>
        </Box>
    );
}

    export default PasswordResetComponent;
