import { BrowserRouter } from "react-router-dom";
import React from "react";
import ReactDOM from "react-dom";

import Main from "./components/main";

function App() {
    return (
        <div className="App">
            { /*<Navbar /> */}
            <Main />        
        </div>
    );
}

ReactDOM.render((
        <BrowserRouter>
            <App /> {/* The various pages will be displayed by the "Main" component */}
        </BrowserRouter>
    ), document.getElementById("react-mountpoint")
)