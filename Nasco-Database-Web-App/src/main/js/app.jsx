import React, { Component } from "react";
import ReactDOM from 'react-dom';

class Index extends Component {

    constructor(props) {
        super(props);
        this.state = { employees: []};
    }

    componentDidMount() {
        client({ method: 'GET', path: '/demo/all' }).done(response => {
                this.setState({ employees: response.entity._embedded.employees });
            })
    }

    render() {
        return (
            <RusalLineItemList rusalLineItems = { this.state.rusalLineItems }/>
        )
    }
}

class RusalLineItemList extends Component {
    render() {
        const rusalLineItems = this.props.rusalLineItems.map(rusalLineItem =>
                <RusalLineItem key=
                    { rusalLineItem._links.self.href } rusalLineItem={ rusalLineItem }/>
            );
            return(
                <table>
                    <tbody>
                        <tr>
                            <th>Heat Number</th>
                            <th>Gross Weight Kg</th>
                            <th>Net Weight Kg</th>
                            <th>BL Number</th>
                            <th>Grade</th>
                            <th>Dimensions</th>
                            <th>Work Order</th>
                            <th>Load Number</th>
                            <th>Loader</th>
                            <th>Load Time</th>
                        </tr>
                        { rusalLineItems }
                    </tbody>
                </table>
            )
    }
}

class RusalLineItem extends Component {
    render() {
        return (
            <tr>
                <td>{ this.props.rusalLineItem.heatNum }</td>
                <td>{ this.props.rusalLineItem.grossWeightKg }</td>
                <td>{ this.props.rusalLineItem.netWeightKg }</td>
                <td>{ this.props.rusalLineItem.blNum }</td>
                <td>{ this.props.rusalLineItem.grade }</td>
                <td>{ this.props.rusalLineItem.dimensions }</td>
                <td>{ this.props.rusalLineItem.workOrder }</td>
                <td>{ this.props.rusalLineItem.loadNum }</td>
                <td>{ this.props.rusalLineItem.loader }</td>
                <td>{ this.props.rusalLineItem.loadTime }</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <Index />,
    document.getElementById('react-mountpoint')
);