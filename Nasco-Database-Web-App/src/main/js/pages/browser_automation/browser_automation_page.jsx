import React from "react";
import { Switch, Route, Link, useRouteMatch } from "react-router-dom";

import ToBeImplemented from "../../core/to_be_implemented";
import AlgomaReception from "./receptions/algoma/alogoma_reception";

export default function BrowserAutomationPage() {
    let { path, url } = useRouteMatch();

    return (
        <div className='center'>
            <h1>Browser Automation</h1>
            <ToBeImplemented />
            <h2>Functions</h2>
            <Switch>
                <Route exact path={ path }>
                    <h3>Please Select a Function.</h3>
                </Route>
                <Route path={ `${path}/algoma` }>
                    <AlgomaReception />
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