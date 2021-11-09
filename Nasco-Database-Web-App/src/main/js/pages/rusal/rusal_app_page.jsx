import React, { useEffect, useState } from "react";
import { Link, Route, Switch, useRouteMatch } from "react-router-dom";
import GetReceptionProgress from "./components/get_reception_progress";
import RusalLineItemList from "./components/rusal_line_item_list";
import UploadPackingList from "./components/upload_packing_list";
import RusalDownloads from "./rusal_downloads";
import RusalUpdateItem from "./rusal_update_item";

export default function RusalPage() {

    let { path, url } = useRouteMatch();

    const [rusalLineItems, setRusalLineItems] = useState([]);
    
    useEffect(() => {
        fetchRusalInventoryItems();
    }, []);

    function fetchRusalInventoryItems() {

        fetch("/api/rusal/recent")
            .then(res => res.json())
            .then(
                (response) => {
                    setRusalLineItems(response)
                },
                (error) => {
                    alert(error);
                }
            )
    }

    return (
        <div className='center' id = "rusal-all">
            <Switch>
                <Route exact path={path}>
                    <h3>Please Choose a Function</h3>
                </Route>
                <Route path={`${path}/add`}>
                    <h3>Add Rusal Items</h3>
                    &nbsp;&nbsp;&nbsp;
                    <UploadPackingList /> 
                </Route>
                <Route path={`${path}/update`}>
                    <RusalUpdateItem />
                </Route>
                <Route path={`${path}/downloads`}>
                    <RusalDownloads />
                </Route>
            </Switch>
            <ul>
                <li>
                    <Link to={`${url}/add`}>Add Inventory Items</Link>
                </li>
                <li>
                    <Link to={`${url}/update`}>Update Inventory Item</Link>
                </li>
                <li>
                    <Link to={`${url}/downloads`}>Download Options</Link>
                </li>
            </ul>
            <GetReceptionProgress />
            <h1>Rusal Inventory Items</h1>
            <RusalLineItemList rusalLineItems={ rusalLineItems } />
        </div>
    );
}