import './App.css';
import AppBar from './components/AppBar';
import LabListComponents from './components/LabListComponents';
import Header from './components/Header';
import Footer from './components/Footer';
import {BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AddLabComponent from './components/AddLabComponent';
import LoginForm from './components/Login';
import {checkAuthentication} from './services/AuthHeader'
import EditLabComponent from './components/EditLabComponent';
import RunCommandComponent from './components/RunCommandComponent';

function App() {
  checkAuthentication();
  return (
    <div className="App">
      <Router>
      <Header />
      <div className= "container">
        <Routes>
          <Route exact path = "/" element = {<LabListComponents/>}></Route>
          <Route path = "/labs" element = {<LabListComponents/>}></Route>
          <Route path = "/add-lab" element = {<AddLabComponent/>}></Route>
          <Route path="/login" element={<LoginForm/>}></Route>
          <Route path="/edit-lab/:labName" element={<EditLabComponent/>}></Route> 
          <Route path="/run-command/:labName" element={<RunCommandComponent/>}></Route>
        </Routes>
      </div>   
      <Footer />
      </Router>
    </div>
  );
}

export default App;
