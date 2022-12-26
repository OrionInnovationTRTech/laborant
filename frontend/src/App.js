import './App.css';
import LabListComponents from './components/LabListComponents';
import Header from './components/Header';
import Footer from './components/Footer';
import {BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AddLabComponent from './components/AddLabComponent';
import LoginForm from './components/Login';
import {checkAuthentication} from './services/AuthHeader'
import EditLabComponent from './components/EditLabComponent';
import RunCommandComponent from './components/RunCommandComponent';
import BulkAddLabComponent from './components/BulkAddLabComponent';
import UserListComponents from './components/UserListComponents';
import AddUserComponent from './components/AddUserComponent';
import NotFound from './components/NotFound';

function App() {
  checkAuthentication();
  function isAdmin() {
    const username = localStorage.getItem('username');
    if (username === 'admin') {
      return true;
    } else {
      return false;
    }
  }  
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
          <Route path="/bulk-add" element={<BulkAddLabComponent/>}></Route>
          <Route path='/users' element={
              isAdmin() ? (
                <UserListComponents />
              ) : (
                <NotFound/>
              )
            }
          />
          <Route path="/add-user"  element={
              isAdmin() ? (
                <AddUserComponent />
              ) : (
                <NotFound/>
              )
            }
          />
          <Route path="*" element={<NotFound/>}></Route>          
        </Routes>
      </div>   
      <Footer />
      </Router>
    </div>
  );
}

export default App;
