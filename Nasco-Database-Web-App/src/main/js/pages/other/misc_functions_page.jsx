import React from "react";
import { Switch, Route, Link, useRouteMatch} from "react-router-dom";
import ToBeImplemented from "../../core/to_be_implemented";
import FeaturesAndBugs from "../../apps/misc_functions/features_and_bugs";
import ExcelSpreadSheetFormatting from "./excel/excel_spreadsheet_formatting";

export default function MiscFunctionsPage() {
    let { path, url } = useRouteMatch();

    return (
        <div>
            <h1>Misc Functions Page</h1>
            <ToBeImplemented />
            <h2>Functions</h2>
            <ul>
                <li>
                    <Link to={`${url}/logging`}>Features and Bugs</Link>
                </li>
                <li>
                    <Link to={`${url}/excel_formatting`}>Excel SpreadSheet Formatting</Link>
                </li>
            </ul>

            <Switch>
                <Route exact path={path}>
                    <h3>Choose a Function</h3>
                </Route>
                <Route path={`${path}/logging`}>
                    <BugReporting />
                </Route>
                <Route path={`${path}/excel_formatting`}>
                    <ExcelSpreadSheetFormatting />
                </Route>
            </Switch>
        </div>
    );
}