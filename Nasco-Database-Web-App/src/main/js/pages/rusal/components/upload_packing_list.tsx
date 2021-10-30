import React from 'react'
import { trackPromise } from 'react-promise-tracker'

export default function UploadPackingList() {

    function handleSubmit(evt) {

        evt.preventDefault()

        trackPromise (
            fetch('/api/rusal/import/packing-list', {
                method: 'POST',
                body: new FormData(evt.target)
            }).then(
                (response) => {
                    if (response.ok) {
                        alert('Upload of packing list successful!')
                    } else {
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
        <form onSubmit={handleSubmit}>
            <input id='packing-list-input' name='file' type='file' />
            &nbsp;&nbsp;&nbsp;
            <input id='barge' name='barge' type='text' placeholder='Enter Barge Identifier: ' />
            &nbsp;&nbsp;&nbsp;
            <button id='submit' type='submit'>Upload Packing List</button>
        </form>
    )
}