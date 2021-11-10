import { BrowserRouter } from "react-router-dom";
import React from "react";
import ReactDOM from "react-dom";

import LoadingIndicator from "./core/loading_indicator";
import '../css/main.css'
import Main from './main'

function App() {
    return (
        <div className="App">
            { /* <Navbar /> */}
            <LoadingIndicator />
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