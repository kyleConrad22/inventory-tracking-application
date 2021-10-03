import React from "react";
import { Switch, Route } from "react-router-dom";

import LandingPage from "./pages/landing_page";
import RusalPage from "./pages/rusal_app_page";
import MiscFunctionsPage from "./pages/misc_functions_page";
import CheckinCheckoutPage from "./pages/checkin_checkout_app_page";

const Main = () => {
    return (
        <Switch>{/* The Switch decides which component to show based on the current URL */}
            <Route exact path="/">
                <LandingPage />
            </Route>
            <Route path="/rusal"><RusalPage /></Route>
            <Route path='/misc'>
                <MiscFunctionsPage />
            </Route>
            <Route path='/truck_support'>
                <CheckinCheckoutPage />
            </Route>
        </Switch>
    );
}

export default Main;