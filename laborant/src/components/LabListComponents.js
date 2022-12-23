    import React, {useState, useEffect, version} from "react";
    import Table from 'react-bootstrap/Table';
    import Container from 'react-bootstrap/Container';
    import Form from 'react-bootstrap/Form';
    import InputGroup from 'react-bootstrap/InputGroup';
    import {Link} from "react-router-dom";
    import { checkAuthentication, getHeaders } from "../services/AuthHeader";
    import axios from "axios";



    const LabListComponents = () => {
        checkAuthentication();
        
        
        const [labs, setLabs] = useState([]);
        const [labVersion, setLabVersion] = useState({});
        const [isMulti, setIsMulti] = useState({});
        const [search, setSearch] = useState('');



        useEffect(() => {


            getAllLabs();
        }, []);

        const getAllLabs = () => {
            axios.get('http://localhost:8080/v1/labs/', getHeaders())
            .then((response) => {
                setLabs(response.data);
                console.log(response.data);
            }).catch((error) => {
                console.log(error);
            })
        }
        useEffect(() => {
            let labVersion = "CAN'T CONNECT";
            let isMulti = "false";
            labs.forEach(lab => {
              axios.get(`http://localhost:8080/v1/labs/runCommand/${lab.labName}?command=sudo+wae-status`, getHeaders())
                .then((response) => {
                    const data = response.data.split('\n').map(row => {
                        const elements = row.split(' ').filter(element => element !== '');
                        return elements;
                        
                      }).filter(row => row.length > 0);
                        console.log(data);
                        
                        if (data[0][1] === "connect") {
                            console.log(labVersion);
                        }
                        else{
                            labVersion = data[1][1];
                            console.log(labVersion);
                        }
                        if (data[2][1] !== "") {
                            console.log(isMulti);
                            isMulti = "true";
                        }
                        
                }).catch((error) => {
                    console.log(error);
                });
                setLabVersion((prevLabVersions) => ({ ...prevLabVersions, [lab.labName]: labVersion }));
                setIsMulti((prevIsMulti) => ({ ...prevIsMulti, [lab.labName]: isMulti }));
            });
          }, [labs]);
        

        const deleteLab = (labName) => {
    if (window.confirm("Are you sure you want to delete this lab?")) {
        axios.delete("http://localhost:8080/v1/labs/" + labName, getHeaders())
            .then((response) => {
                console.log(response);
                window.location.replace('/labs');
            }).catch((error) => {
                console.log(error);
            })
    }
}

            return(
                <div>
                    <h1 className = "text-center"> Lab List<Link to="/add-lab" className="btn btn-primary" style={{marginLeft: '700px'}}>Add Lab</Link></h1>

                     <Container>
                        <Form>
                        <InputGroup className='my-3'>
                            <Form.Control
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder='Filter labs...'
                            />
                        </InputGroup>
                        </Form>
                        <Table bordered hover>
                        <thead>
                            <tr>
                            <th>Name</th>
                            <th>Username</th>
                            <th>Password</th>
                            <th>Host</th>
                            <th>Port</th>
                            <th>Version</th>
                            <th>isMulti</th>
                            <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        
                                {labs
                                    .filter((labs) => {
                                      return search.toLowerCase() === ''
                                        ? labs
                                        : labs.labName.toLowerCase().includes(search) ||
                                        labs.userName.toLowerCase().includes(search) ||
                                        labs.password.toLowerCase().includes(search) ||
                                        labs.host.toLowerCase().includes(search) ||
                                        labVersion[labs.labName].toLowerCase().includes(search);
                                        
                                    }).map(
                                    labs => 
                                    <tr key={labs.labName}>
                                        <td>{labs.labName}</td>
                                        <td>{labs.userName}</td>
                                        <td style={{ color: labs.password === "hidden" ? "gray" : "black" }}>
                                        {labs.password}</td>
                                        <td>{labs.host}</td>
                                        <td>{labs.port}</td>
                                        <td>{labVersion[labs.labName]}</td>
                                        <td>{isMulti[labs.labName]}</td>
                                        <td>
                                            <button onClick={() => deleteLab(labs.labName)} className="btn btn-danger">Delete</button>
                                            <Link className="btn btn-secondary" to={`/edit-lab/${labs.labName}`}>Edit</Link>
                                            <Link className="btn btn-success" to={`/run-command/${labs.labName}`}>Run Command</Link>
                                        </td>
                                    </tr>
                                )
                            }

                        </tbody>
                        </Table>
                    </Container>
        
                    

                </div>

            )
        }


    export default LabListComponents;