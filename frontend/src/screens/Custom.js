import React, { Component } from 'react';
import { ActivityIndicator, FlatList, Text, View, TextInput, TouchableOpacity, StyleSheet, Alert, Image} from 'react-native';
import Icon from '@expo/vector-icons/AntDesign';
import { NavigationActions } from 'react-navigation';

export default class Custom extends Component {
  constructor(props) {
    super(props);

    this.state = {
      data: [],
      isLoading: true,
      error: null,
      longURL: '',
      custom: '' 
    };
  }

  updateLongURL = (longURL) => {
    this.setState({longURL});
  };

  updateCustom = (custom) => {
    this.setState({custom});
  };


  render() {
    const { data, isLoading } = this.state;
    const { navigate } = this.props.navigation;
    return (
    <View style={{ backgroundColor: "#f9c14a", height: "100%" }}>
                <View style={{
                    flexDirection: "row", 
                    marginTop: 35,
                    paddingVertical: 2,
                    
                }}>
                   
                   <Icon onPress={() => navigate('Main')}
                        style={{
                           marginLeft: 10,
                           alignSelf:"flex-end"
                       }} name="back" color="#000" size={30}/>
                    
                    <Text
                        style={{
                            fontSize: 25,
                            fontFamily: "SemiBold",
                            marginRight: "auto",
                            marginLeft: "auto",
                            alignSelf:"center",
                            color: "#f9c14a"

                        }}
                    >URL-SHORTENER</Text>
                    <Icon style={{
                            marginTop: 10,
                            marginRight: 20,
                            alignSelf:"center"

                        }}
                        name="logout" color="#000" size={30}
                        onPress={() =>
                            Alert.alert(
                                "Loging Out",
                                "Are you sure to log out?",
                                [
                                    {
                                        text: "Cancel",
                                        style: "cancel"
                                    },
                                    { text: "OK", onPress: () => 
                                    this.props.navigation.reset([NavigationActions.navigate({routeName:'Login'})])
                                    //navigate('Login') 
                                    }
                                ]
                            )} />
                </View>
                
                <Image source={require('../images/url_logo.png')}
                    style={{ width: "75%", height: "25%", alignSelf: "center", marginTop: 30 }}
                /> 
                

            <View style={styles.topBtnContainer2}>

                <TextInput
                    style={{
                        marginTop: 0,
                        color: "#000",
                        fontFamily: "SemiBold",
                        paddingVertical: 5,
                        paddingHorizontal: 5,
                        fontSize: 15,
                        flex: 1,
                        marginRight: 10,
                        marginLeft: 10,
                        textAlign: "left",

                        flexDirection: "row",
                        borderWidth: 4,
                        marginTop: 0,
                        paddingHorizontal: 5,
                        borderColor: "#000",
                        }}

                        placeholder="Enter URL..."
                        placeholderTextColor="#000"
                        onChangeText={this.updateLongURL}
                        value={this.state.longURL}
                    >
                </TextInput> 
            </View>


            <View style={styles.topBtnContainer2}>
                <TextInput
                    style={{
                        marginTop: 0,
                        color: "#000",
                        fontFamily: "SemiBold",
                        paddingVertical: 5,
                        paddingHorizontal: 5,
                        fontSize: 15,
                        flex: 1,
                        marginRight: 10,
                        marginLeft: 10,
                        textAlign: "left",

                        flexDirection: "row",
                        borderWidth: 4,
                        marginTop: 0,
                        paddingHorizontal: 5,
                        borderColor: "#000",
                        }}

                        placeholder="Enter Custom URL..."
                        placeholderTextColor="#000"
                        onChangeText={this.updateCustom}
                        value={this.state.custom}
                    >
                </TextInput>    
            </View>


            <View style={styles.topBtnContainer4}>       
            <Icon    
                onPress={() => {
                    fetch(`http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/links?userID=${window.id}&url=${this.state.longURL}&customKey=${this.state.custom}`,{ method: 'POST', headers: { Accept: 'application/json', 'Content-Type': 'application/json' }})
                    .then((response) => response.json())
                    .then((json) => {
                        this.setState({ data: json });
                        if(json.status == 200){
                            this.props.navigation.reset([NavigationActions.navigate({routeName:'Main'})])
                             //navigate('Main') 
                            }
                        else
                        {
                            Alert.alert(
                                "Problem Occured!",
                                json.message,
                                [
                                    {
                                        text: "OK"
                                    }
                                ]
                            )
                        }
                    })
                    .catch((error) => console.error(error))
                    .finally(() => {
                        this.setState({ isLoading: false });
                    });    
                 }}

                    style={{
                        marginTop: 0,
                        alignSelf: "center",
                        color: "#ed5c61",
                        fontFamily: "SemiBold",
                        paddingVertical: 0,
                        marginHorizontal: 20,
                    }}
                    
                    name="checkcircle" color="#ed5c61" size={40} />     
            </View>

      </View>
        );
    }
}

const styles = StyleSheet.create({
    topBtnContainer4: {
        marginHorizontal: 0,
        marginTop: 20,
        paddingVertical: 2,
        width: "100%"
    },
    topBtnContainer3: {
        paddingVertical: 2,
        flexDirection: "row",
        justifyContent: "space-between",
        width: "100%",
        alignItems: "center",
        marginTop: 5,
    },
    topBtnContainer2: {
        marginHorizontal: 0,
        marginTop: 25,
        paddingVertical: 2,
        flexDirection: "row",
        width: "100%"
    },
    topBtnContainer: {
        paddingVertical: 10,
        flexDirection: "row",
        justifyContent: "space-between",
        width: "100%",
        alignItems: "center"
    },
    topUserBtn: {
        backgroundColor: "white",
        borderRadius: 10,
        paddingHorizontal: 20,
        marginLeft: "auto",
        marginRight: "auto",
        flexDirection: "row",
        justifyContent: 'space-between'
    },
    topBtnText: {
        color: "#961B92",
        fontFamily: "SemiBold",
        textAlign: "center",
        fontSize: 15,
        paddingHorizontal: 10
    },
    detailsBtn: {
        alignSelf: "center",
        backgroundColor: "#961B92",
        borderRadius: 10,
        padding: 10,
        width: "25%"
    },

    container: {
        bottom: 0
    },

});
