import React from "react";
import { trackPromise } from "react-promise-tracker";

export default function AlgomaForm( ) {

    function handleSubmit(evt) {

        evt.preventDefault();
        trackPromise (
            fetch('/api/browser/reception/algoma', {
                method: 'POST',
                body: new FormData(evt.target)
            }).then(
                (response) => {
                if(!response.ok) {
                    alert('Something went wrong!')
                }
            }
            ).catch(
                (error) => {
                    alert(error)
                }
            )
        )
        evt.target.reset()
        return false
    }

    return (
        <div>
            <form onSubmit={ handleSubmit }>
                <input id='username' name='username' type='text' placeholder='Enter TC3 Username' />
                &nbsp;&nbsp;&nbsp;
                <input id='password' name='password' type='text' placeholder='Enter TC3 Password' />
                &nbsp;&nbsp;&nbsp;
                <input id='files' name='files' type='file' multiple />
                &nbsp;&nbsp;&nbsp;
                <input id='offload-date' name='offloadDate' type='text' placeholder='Enter Railcar Offload Date' />
                &nbsp;&nbsp;&nbsp;
                <button id='submit'>Create Algoma Reception</button>
            </form>
        </div>
    );
}