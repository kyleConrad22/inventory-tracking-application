import React from "react";
import { Switch, Route, Link, useRouteMatch } from "react-router-dom";

import ToBeImplemented from "../../core/to_be_implemented";
import CustomerSettings from "./customer_settings";
import RunTruckSupport from "./run_truck_support";
import NewCustomer from "./new_customer";

export default function CheckinCheckoutPage() {
    let { path, url } = useRouteMatch();

    return (
        <div>
            <h1>Truck Support Appliction (Check-In / Check-Out)</h1>
            <ToBeImplemented />
            <ToBeImplemented />
            <h2>Functions</h2>
            <ul>
                <li>
                    <Link to={ `${url}/settings` }>Modify Customer Process</Link>
                </li>
                <li>
                    <Link to={ `${url}/run` }>Run Check-In / Check-out App</Link>
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
                    <RunTruckSupport />
                </Route>
                <Route path={ `${path}/new` }>
                    <NewCustomer />
                </Route>
            </Switch>
        </div>
    );
}