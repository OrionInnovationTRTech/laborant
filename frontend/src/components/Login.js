import React from 'react';
import axios from "axios";
import {Link} from "react-router-dom";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';

const theme = createTheme();

class Logout extends React.Component {
    state = {
        isAuthenticated: false,
    };

    logout = () => {
        localStorage.clear();
        window.location.replace('http://localhost:3000/login')
        this.setState({
            isAuthenticated: false,
        });
    }

    render() {
        return (
            <button onClick={this.logout}>Logout</button>
        );
    }
}

class LoginForm extends React.Component {
    state = {
        username: '',
        password: '',
        isAuthenticated: false,
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value,
        });
    }

    handleSubmit = (event) => {
        event.preventDefault();

        const {username, password} = this.state;

        axios.get(`${process.env.REACT_APP_SPRING_HOST}/users/${username}/login`, {
            headers: {
                'Authorization': 'Basic ' + btoa(username + ':' + password),
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    if (response.data.user_role === "ADMIN") {
                        localStorage.setItem('isAdmin', true);
                    } else {
                        localStorage.setItem('isAdmin', false);
                    }
                    if (response.data.email !== null) {
                        if (response.data.email !== "") {
                            localStorage.setItem('hasEmail', true);
                        } else {
                            localStorage.setItem('hasEmail', false);
                        }
                    }
                    // If the authentication is successful, save the username and password in local storage
                    localStorage.setItem('username', username);
                    localStorage.setItem('password', password);
                    this.setState({
                        isAuthenticated: true,
                    });
                    localStorage.setItem('isAuthenticated', true);
                    if (localStorage.getItem('isAuthenticated')) {
                        if (localStorage.getItem('hasEmail') === "true") {
                            window.location.replace('/');
                        } else {
                            window.location.replace('/dashboard');
                        }
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

    render() {
        const {username, password, isAuthenticated} = this.state;

        if (localStorage.getItem('isAuthenticated')) {
            // If the user is authenticated, show a message
            return <p>You are logged in as {localStorage.getItem('username')}.</p>;
        }

        return (
            <ThemeProvider theme={theme}>
                <Container component="main" maxWidth="xs">
                    <CssBaseline/>
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
                        <Box component="form" onSubmit={this.handleSubmit} noValidate sx={{mt: 1}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                id="username"
                                label="Username"
                                name="username"
                                autoComplete="username"
                                autoFocus
                                value={this.state.username}
                                onChange={this.handleChange}
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
                                value={this.state.password}
                                onChange={this.handleChange}
                            />
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                sx={{mt: 3, mb: 2}}
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
}


export default LoginForm;
export {Logout};

