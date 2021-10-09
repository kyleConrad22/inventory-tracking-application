import React from 'react'

export default function FetchRequest(url : string, callback : (response : any) => void) : void {
    fetch(url)
        .then(res => res.json)
        .then(
            (response) => {
                callback(response)
            }, 
            (error) => {
                alert(error)
            }
        )
}