import React from 'react';
import { Text, View, Image, TextInput, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import Icon from '@expo/vector-icons/AntDesign';
window.id = {};

export default class Login extends React.Component {

    constructor(props) {
        super(props);
    
        this.state = {
          data: [],
          isLoading: true,
          error: null,
          email: '',
          password: ''
        };
      }

      updateEmail = (email) => {
        this.setState({email});
      };

      updatePassword = (password) => {
        this.setState({password});
      };

/*
    componentDidMount() {
        console.log("in login mount");
        fetch(`https://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/user?email=${email}&password=${password}`)
          .then((response) => response.json())
          .then((json) => {
            this.setState({ data: json });
          })
          .catch((error) => console.error(error))
          .finally(() => {
            this.setState({ isLoading: false });
          });  
      }
*/

    render() {
        const { navigate } = this.props.navigation;
        //console.log("in login render");
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
                    marginTop: 75,
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

                <View>
                    <TouchableOpacity
                        style={styles.loginBtn}>
                        <Text
                            onPress={() => {
                            fetch(`http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/user?email=${this.state.email}&password=${this.state.password}`)
                            .then((response) => response.json())
                            .then((json) => {
                                this.setState({ data: json });
                                window.id = json.userID;
                                if(json.status == 200){navigate('Main');}
                                else{
                                    Alert.alert(
                                        "Invalid Data Provided",
                                        "Either email or password is incorrect. Be sure to provide valid information.",
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

                            style={styles.loginBtnText}>Sign In</Text>
                    </TouchableOpacity>
                </View>

                <Text
                    onPress={() => navigate('Register')}

                    style={{
                        marginTop: 20,
                        alignSelf: "center",
                        color: "#eb4067",
                        fontFamily: "SemiBold",
                        paddingVertical: 15,
                        fontSize: 18
                    }}>Sign Up!</Text>

            </View>
        );
    }
}


const styles = StyleSheet.create({
    loginBtnText: {
        color: "white",
        fontFamily: "SemiBold",
        textAlign: "center",
        fontSize: 18
    },
    loginBtn: {
        marginVertical: 30,
        alignSelf: "center",
        backgroundColor: "#ed5c61",
        borderRadius: 50,
        padding: 10,
        width: "35%"
    }
});