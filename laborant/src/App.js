import './App.css';
import AppBar from './components/AppBar';
import LabListComponents from './components/LabListComponents';
import SignIn from './components/SignIn';
import Header from './components/Header';
import Footer from './components/Footer';
import {BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AddLabComponent from './components/AddLabComponent';
import Login from './components/Login';
import LoginForm from './components/asd';
import {Logout} from './components/asd';
import {checkAuthentication} from './services/AuthHeader'

function App() {
  checkAuthentication();
  return (
    <div className="App">
      <Router>
      <Header />
      <Logout />
      <div className= "container">
        <Routes>
          <Route exact path = "/" element = {<LabListComponents/>}></Route>
          <Route path = "/labs" element = {<LabListComponents/>}></Route>
          <Route path = "/add-lab" element = {<AddLabComponent/>}></Route>
          <Route path="/login" element={<LoginForm/>}></Route>
        </Routes>
      </div>   
      <Footer />
      </Router>
    </div>
  );
}

export default App;
