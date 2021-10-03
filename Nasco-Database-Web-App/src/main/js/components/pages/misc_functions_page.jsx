import React from "react";
import { Switch, Route, Link, useRouteMatch} from "react-router-dom";
import ToBeImplemented from "../util/to_be_implemented";
import BugReporting from "../apps/misc_functions/bug_reporting";
import ExcelSpreadSheetFormatting from "../apps/misc_functions/excel_spreadsheet_formatting";

export default function MiscFunctionsPage() {
    let { path, url } = useRouteMatch();

    return (
        <div>
            <h1>Misc Functions Page</h1>
            <ToBeImplemented />
            <h2>Functions</h2>
            <ul>
                <li>
                    <Link to={`${url}/bugs`}>Bug Reporting</Link>
                </li>
                <li>
                    <Link to={`${url}/excel_formatting`}>Excel SpreadSheet Formatting</Link>
                </li>
            </ul>

            <Switch>
                <Route exact path={path}>
                    <h3>I am Confusion</h3>
                </Route>
                <Route path={`${path}/bugs`}>
                    <BugReporting />
                </Route>
                <Route path={`${path}/excel_formatting`}>
                    <ExcelSpreadSheetFormatting />
                </Route>
            </Switch>
        </div>
    );
}