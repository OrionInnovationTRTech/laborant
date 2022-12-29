// This function will check if the user is authenticated and redirect to the login page if they are not
export const checkAuthentication = () => {
    if (!localStorage.getItem("isAuthenticated") && window.location.pathname !== "/login") {
      window.location.replace("/login");
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

  