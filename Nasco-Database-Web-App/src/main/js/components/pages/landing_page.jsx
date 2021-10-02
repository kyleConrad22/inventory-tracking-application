import React from "react";
import { Link, useHistory } from "react-router-dom";
import MiscFunctionsPage from "./misc_functions_page";

export default function LandingPage() {

    function RusalButton() {
        let history = useHistory();

        function handleClick() {
            history.push("/rusal");
        }

        return (
            <button id="rusal-button" type="button" onClick={ () => { handleClick() } }>
                Rusal Page
            </button>
        );
    }

    function TruckSupportButton() {
        let history = useHistory();

        function handleClick() {
            history.push("/truck_support");
        }

        return (
            <button id="truck-support-button" type="button" onClick={ () => { handleClick() } }>
                Truck Support App
            </button>
        )
    }

    function MiscFunctionsButton() {
        let history = useHistory();

        function handleClick() {
            history.push("/misc");
        }

        return (
            <button id="misc-button" type="button" onClick={ () => { handleClick() } }>
                Misc Functions
            </button>
        )
    }

    console.log("Made it to Landing Page!")
    return (
        <div>
            <h1>Landing Page</h1>
            <h2>Navigation Buttons Found Below</h2>
            <RusalButton />
            <TruckSupportButton />
            <MiscFunctionsButton />
        </div>
    );
}