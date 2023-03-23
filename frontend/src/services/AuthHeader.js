// This function will check if the user is authenticated and redirect to the login page if they are not
const base = process.env.REACT_APP_BASE_PATH || '';
export const checkAuthentication = () => {
    if (!localStorage.getItem("isAuthenticated") && window.location.pathname !== base + "/login") {
        window.location.replace(base+`/login`);
    }
    else {
        if (!localStorage.getItem("hasEmail")  && window.location.pathname !== base+"/dashboard") {
            window.location.replace(base+"/dashboard");
        }
    }
};


  // This function will return the headers object with the authorization header
  export const getHeaders = () => {
    const username = localStorage.getItem("username");
    const password = localStorage.getItem("password");
  
  
    return {
      headers: {
       'Authorization': 'Basic ' + btoa(username + ':' + password),
      }
    };
  };

  