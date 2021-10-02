import React from "react";
import { BrowserRouter, Switch, Route, Link, userParams, useRouteMatch} from "react-router-dom";

import ToBeImplemented from "../util/to_be_implemented";
import BugReportPage from "../apps/checkin_checkout_app/bug_report_page";
import ExcelSpreadSheetPage from "../apps/checkin_checkout_app/excel_spreadsheet_page";

export default function CheckinCheckoutPage() {
    let { path, url } = useRouteMatch();

    return (
        <div>
            <h1>Truck Support Appliction (Check-In / Check-Out)</h1>
            <ToBeImplemented />
            <h2>Functions</h2>
            <ul>
                <li>
                    <Link to={ `${url}/bugs` }>Bug Reporting</Link>
                </li>
                <li>
                    <Link to={ `${url}/excel_formatting` }>Excel SpreadSheet Formatting</Link>
                </li>
            </ul>

            <Switch>
                <Route exact path={ path }>
                    <h3>Please Select a Function.</h3>
                </Route>
                <Route exact path={ `${path}/bugs` }>
                    <BugReportPage />
                </Route>
                <Route exact path={ `${path}/excel_formatting` }>
                    <ExcelSpreadSheetPage />
                </Route>
            </Switch>
        </div>
    );
}