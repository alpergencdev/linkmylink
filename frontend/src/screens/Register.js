import React from 'react';
import { Text, View, Image, TextInput, TouchableOpacity, StyleSheet, ScrollView, Alert } from 'react-native';
import Icon from '@expo/vector-icons/AntDesign';
import { NavigationActions } from 'react-navigation';

export default class Register extends React.Component {

    constructor(props) {
        super(props);
    
        this.state = {
          data: [],
          isLoading: true,
          error: null,
          email: '',
          password: '',
          passwordAgain: '',
        };
      }


    updateEmail = (email) => {
        this.setState({email});
    };

    updatePassword = (password) => {
        this.setState({password});
    };

    updatePasswordAgain = (passwordAgain) => {
        this.setState({passwordAgain});
    };


    render() {
        const { navigate } = this.props.navigation;

        return (
            <View style={{ backgroundColor: "#f9c14a", height: "100%" }}>

                <Image source={require('../images/url_logo.png')}
                    style={{ width: "75%", height: "25%", alignSelf: "center", marginTop: 80 }}
                />

                <View style={{
                    flexDirection: "row",
                    alignItems: "center",
                    marginHorizontal: 55,
                    borderWidth: 4,
                    marginTop: 50,
                    paddingHorizontal: 10,
                    borderColor: "#000",
                    borderRadius: 0,
                    paddingVertical: 2
                }}>
                    <Icon name="mail" color="#000" size={24} />
                    <TextInput
                        placeholder="Email"
                        placeholderTextColor="#000"
                        style={{ paddingHorizontal: 10 }}
                        onChangeText={this.updateEmail}
                    />

                </View>

                <View style={{
                    flexDirection: "row",
                    alignItems: "center",
                    marginHorizontal: 55,
                    borderWidth: 4,
                    marginTop: 15,
                    paddingHorizontal: 10,
                    borderColor: "#000",
                    borderRadius: 0,
                    paddingVertical: 2
                }}>
                    <Icon name="lock" color="#000" size={24} />
                    <TextInput
                        secureTextEntry
                        placeholder="Password"
                        placeholderTextColor="#000"
                        style={{ paddingHorizontal: 10 }}
                        onChangeText={this.updatePassword}
                    />

                </View>

                <View style={{
                    flexDirection: "row",
                    alignItems: "center",
                    marginHorizontal: 55,
                    borderWidth: 4,
                    marginTop: 15,
                    paddingHorizontal: 10,
                    borderColor: "#000",
                    borderRadius: 0,
                    paddingVertical: 2
                }}>
                    <Icon name="lock" color="#000" size={24} />
                    <TextInput
                        secureTextEntry
                        placeholder="Password Again"
                        placeholderTextColor="#000"
                        style={{ paddingHorizontal: 10 }}
                        onChangeText={this.updatePasswordAgain}
                    />

                </View>

                <View>
                    <TouchableOpacity
                        style={styles.signUpBtn}>
                        <Text
                            onPress={() => {
                                if(this.state.password == this.state.passwordAgain){
                                fetch(`http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/user?email=${this.state.email}&password=${this.state.password}&userType=B2C`,{ method: 'POST', headers: { Accept: 'application/json', 'Content-Type': 'application/json' }})
                                .then((response) => response.json())
                                .then((json) => {
                                    this.setState({ data: json });
                                    if(json.status == 200){
                                        Alert.alert(
                                            "Account Created",
                                            "Your account has been created.",
                                            [
                                                { text: "OK", onPress: () => { 
                                                    this.props.navigation.reset([NavigationActions.navigate({routeName:'Login'})]) 
                                                } }
                                            ]
                                        )
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
                                }
                                else{
                                    Alert.alert(
                                        "Passwords Do Not Match",
                                        "Please be sure to provide same passwords for both fields.",
                                        [
                                            { text: "OK", onPress: () => { 
                                                navigate("Register") 
                                            } }
                                        ]
                                    )
                                }
                                }}

                            style={styles.signUpBtnText}>Sign Up</Text>
                    </TouchableOpacity>
                </View>

                <Text
                    onPress={() => navigate('Login')}

                    style={{
                        marginTop: 5,
                        alignSelf: "center",
                        color: "#eb4067",
                        fontFamily: "SemiBold",
                        paddingVertical: 15,
                        fontSize: 18
                    }}>Sign In!</Text>


            </View>
        );
    }
}

const styles = StyleSheet.create({
    signUpBtnText: {
        color: "white",
        fontFamily: "SemiBold",
        textAlign: "center",
        fontSize: 18
    },
    signUpBtn: {
        marginVertical: 30,
        alignSelf: "center",
        backgroundColor: "#ed5c61",
        borderRadius: 50,
        padding: 10,
        width: "35%"
    }
});