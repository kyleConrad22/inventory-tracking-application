import React from "react";
import { Switch, Route, Link, useRouteMatch } from "react-router-dom";

import CustomerSettings from "./components/customer_settings";
import NewCustomer from "./components/new_customer";
import BrowserAutomation from "./components/browser_automation";

export default function CheckinCheckoutPage() {
    let { path, url } = useRouteMatch();

    return (
        <div>
            <h1>Truck Support Appliction (Check-In / Check-Out)</h1>
            <h2>Functions</h2>
            <ul>
                <li>
                    <Link to={ `${url}/settings` }>Modify Customer Process</Link>
                </li>
                <li>
                    <Link to={ `${url}/run` }>Browser Automation</Link>
                </li>
                <li>
                    <Link to={ `${url}/new` }>Add Customer Process</Link>
                </li>
            </ul>

            <Switch>
                <Route exact path={ path }>
                    <h3>Please Select a Function.</h3>
                </Route>
                <Route path={ `${path}/settings` }>
                    <CustomerSettings />
                </Route>
                <Route path={ `${path}/run` }>
                    <BrowserAutomation />
                </Route>
                <Route path={ `${path}/new` }>
                    <NewCustomer />
                </Route>
            </Switch>
        </div>
    );
}