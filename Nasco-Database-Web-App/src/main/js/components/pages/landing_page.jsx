import React from "react";
import { Link, useHistory } from "react-router-dom";

export default function LandingPage() {

    function RusalButton() {
        let history = useHistory();

        function handleClick() {
            history.push("/rusal");
        }

        return (
            <button id="rusal-button" type="button" onClick={ () => { handleClick() } }>
                Rusal App
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
        );
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
        );
    }

    console.log("Made it to Landing Page!")
    return (
        <div className='centered'>
            <h2>Landing Page</h2>
            <h3>Navigation Buttons</h3>
            <RusalButton />
            &nbsp;&nbsp;&nbsp;
            <TruckSupportButton />
            &nbsp;&nbsp;&nbsp;
            <MiscFunctionsButton />
        </div>
    );
}