import React, { useEffect, useState } from "react";
import { Switch, Link, Route, useRouteMatch } from "react-router-dom";

import BugList from "./bugs/bug_list";
import FeatureList from "./features/feature_list";
import FetchRequest from "../../../core/fetch_request";

export default function FeaturesAndBugs() {
    
    let { path, url } = useRouteMatch()

    return (
        <div>
            <h3>Features and Bugs</h3>
            <ul>
                <li>
                    <Link to={`${url}/features`}>Features</Link>
                </li>
                <li>
                    <Link to={`${url}/bugs`}>Bugs</Link>
                </li>
            </ul>
            <Switch>
                <Route exact path={path}>
                    <h3>Choose a Log to View</h3>
                </Route>
                <Route path={`${path}/features`}>
                    <Features />
                </Route>
                <Route path={`${path}/bugs`}>
                    <Bugs />
                </Route>
            </Switch>
        </div>
    );
}

function Features() {
    
    const [ featureListItems , setFeatureListItems ] = useState([])

    useEffect(() => {
            FetchRequest('api/features', setFeatureListItems)
        }, 
        []
    )

    return (
        <FeatureList featureListItems={ featureListItems } />
    )
}

function Bugs() {

    const [ bugListItems, setBugListItems ] = useState([])

    useEffect(() => {
            FetchRequest('api/bugs', setBugListItems)
        }, 
        []
    )

    return (
        <BugList bugListItems={ bugListItems } />
    )
}
