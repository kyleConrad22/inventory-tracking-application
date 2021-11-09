/* eslint-disable react/prop-types */
import React, { useState } from 'react'

export default function GetReceptionProgress() {

    const [receivedItemsCount, setReceivedItemCount] = useState(String)
    const [barge, setBarge] = useState(String)

    function fetchReceivedItems(evt) {

        evt.preventDefault()

        const params = new URLSearchParams()
        const input = new FormData(evt.target)

        params.set('barge', input.get('barge').toString())

        setBarge(input.get('barge').toString())

        fetch(`/api/rusal/reception/progress?${params}`)
            .then(res => res.json())
            .then(
                (response) => {
                    console.log(response.response)
                    setReceivedItemCount(response.response)
                }, 
                (error) => {
                    alert(error)
                }
            ).catch (
                (error) => {
                    alert(error)
                }
            )
            evt.target.reset()
            return false
    }

    function DisplayReceivedItemCount(props) {

        if (!props.barge) {
            return (
                <div className='wrapper-padded'>
                    <p>No barge has been selected yet...</p>
                </div>
            )
        }

        if (!props.receivedItemsCount) {
            return (
                <div className='wrapper-padded'>
                    <p>Something went wrong when getting received items for {props.barge}...</p>
                </div>
            )
        }

        return (
            <div className='wrapper-padded'>
                <p>{props.barge} Reception Progress: {props.receivedItemsCount}</p>
            </div>
        )
    }

    return (
        <div>
            <h3>Reception Progress</h3>
            <div className = 'wrapper-inline-row'>
                <form onSubmit={fetchReceivedItems} className = 'wrapper-padded-form'>
                    <input id = 'barge' name = 'barge' type = 'text' placeholder="Enter Barge Identifier:" />
                    <button id='submit-reception' type = 'submit'>Submit</button>
                </form>
                <DisplayReceivedItemCount barge={barge} receivedItemsCount={receivedItemsCount} />
            </div>
        </div>
    )
}