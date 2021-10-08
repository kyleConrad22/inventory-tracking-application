import React, { useEffect, useState } from "react";
import { Switch, Link, Route, useRouteMatch } from "react-router-dom";
import FetchRequest from "../../util/fetch_request";
import ToBeImplemented from "../../util/to_be_implemented";

export default function FeaturesAndBugs() {
    
    let { path, url } = useRouteMatch()

    return (
        <div>
            <h1>Features and Bugs</h1>
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

function BugList(props) {

}