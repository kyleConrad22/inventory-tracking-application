import React from "react";
import { Switch, Route, Link, useRouteMatch } from "react-router-dom";

import ToBeImplemented from "../../core/to_be_implemented";
import AlgomaReceptionButton from "./algoma_reception_button";

export default function BrowserAutomationPage() {
    let { path, url } = useRouteMatch();

    return (
        <div>
            <h1>Browser Automation</h1>
            <ToBeImplemented />
            <h2>Functions</h2>
            <Switch>
                <Route exact path={ path }>
                    <h3>Please Select a Function.</h3>
                </Route>
                <Route path={ `${path}/algoma` }>
                    <AlgomaReceptionButton />
                </Route>
            </Switch>

            <ul>
                <li>
                    <Link to={ `${url}/algoma` }>Algoma Reception</Link>
                </li>
            </ul>
        </div>
    );
}