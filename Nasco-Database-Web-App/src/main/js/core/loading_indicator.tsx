import React from 'react'
import { usePromiseTracker } from 'react-promise-tracker'
import Loader from 'react-loader-spinner'


export const LoadingIndicator = props => {
    const { promiseInProgress } = usePromiseTracker()

    return (
        promiseInProgress &&
        <div
            style = {{
                width: '100%',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                position : 'absolute'
            }}>
            <h3>Loading, this may take some time...</h3>
            <Loader type="ThreeDots" color="#1753D0" height='100' width='100' />
        </div>
    )
}

export default LoadingIndicator