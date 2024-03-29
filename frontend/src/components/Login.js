import React from 'react';
import axios from "axios";
import {Link, useNavigate} from "react-router-dom";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {useState} from "react";
import {useAuth} from "../services/AuthContext";

const theme = createTheme();

function Logout() {
    const navigate = useNavigate();
    const { authState, setAuthState } = useAuth();

    const handleLogout = () => {
        localStorage.clear();
        setAuthState({
            username: null,
            isAdmin: false,
            isAuthenticated: false,
        });

        navigate("/login");
    };

    return (
        <button onClick={handleLogout}>Logout</button>
    );
}


function LoginForm() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();
    const { authState, setAuthState } = useAuth();

    const handleChange = (event) => {
        if (event.target.name === "username") {
            setUsername(event.target.value);
        } else if (event.target.name === "password") {
            setPassword(event.target.value);
        }
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        axios.get(`${process.env.REACT_APP_SPRING_HOST}/users/${username}/login`, {
            headers: {
                'Authorization': 'Basic ' + btoa(username + ':' + password),
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    const isAdmin = response.data.user_role === "ADMIN";
                    const hasEmail = response.data.email && response.data.email !== "";

                    localStorage.setItem('isAdmin', isAdmin);
                    localStorage.setItem('hasEmail', hasEmail);
                    localStorage.setItem('username', username);
                    localStorage.setItem('password', password);
                    localStorage.setItem('isAuthenticated', true);

                    setAuthState({
                        username: username,
                        isAdmin: isAdmin,
                        isAuthenticated: true,
                    });

                    if (hasEmail) {
                        navigate("/");
                    } else {
                        navigate("/dashboard");
                    }
                } else {
                    throw new Error('Error logging in: ' + response.statusText);
                }
            })
            .catch(error => {
                console.log('Error:', error.message);
                alert('Could not authenticated');
            });
    }

    if (isAuthenticated) {
        // If the user is authenticated, show a message
        return <p>You are logged in as {localStorage.getItem('username')}.</p>;
    }

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs">
                <CssBaseline />
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Typography component="h1" variant="h5">
                        Sign in
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="username"
                            label="Username"
                            name="username"
                            autoComplete="username"
                            autoFocus
                            value={username}
                            onChange={handleChange}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            type="password"
                            id="password"
                            autoComplete="current-password"
                            value={password}
                            onChange={handleChange}
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            Sign In
                        </Button>
                        <Grid container>
                            <Grid item xs>
                                <Link to="/forgot-password" variant="body2">
                                    Forgot password?
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
);
}
export default LoginForm;
export {Logout};

