import React from "react";
import { Switch, Route } from "react-router-dom";

import LandingPage from "./pages/landing/landing_page";
import MiscFunctionsPage from "./pages/other/misc_functions_page";
import BrowserAutomationPage from "./pages/browser_automation/browser_automation_page";
import RusalPage from "./pages/rusal/rusal_app_page";

const Main = () => {
    return (
        <Switch>{/* The Switch decides which component to show based on the current URL */}
            <Route exact path="/">
                <LandingPage />
            </Route>
            <Route path="/rusal">
                <RusalPage />
            </Route>
            <Route path='/misc'>
                <MiscFunctionsPage />
            </Route>
            <Route path='/browser'>
                <BrowserAutomationPage />
            </Route>
        </Switch>
    );
}

export default Main;