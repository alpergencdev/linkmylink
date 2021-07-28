import React, { Component } from 'react';
import { SafeAreaView, ActivityIndicator, FlatList, Text, View, TextInput, TouchableOpacity, StyleSheet, Alert, Image, Linking, Clipboard, ToastAndroid} from 'react-native';
import Icon from '@expo/vector-icons/AntDesign';
import { NavigationActions } from 'react-navigation';

export default class Main extends Component {
  constructor(props) {
    super(props);

    this.state = {
      data: [],
      isLoading: true,
      error: null,
      longURL: '',
      copy: '' 
    };
  }

comparator = (item1, item2) => {
    var string1 = item1.creationDate;
    var string2 = item2.creationDate;
    var string1Arr = string1.split("-");
    var string2Arr = string2.split("-");
    for(var i = 2; i >= 0; i--) {
        if(string1Arr[i] < string2Arr[i])
            return 1;
        else if(string1Arr[i] > string2Arr[i]) {
            return -1;
        }
    }
    return 0;
  };


  updateCopy = (copy) => {
    this.setState({copy});
  };

  updateLongURL = (longURL) => {
    this.setState({longURL});
  };

  fetchURL = () => {
    fetch(`http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/links?userID=${window.id}`)
      .then((response) => response.json())
      .then((json) => {
        var key = json.keys;
        key.sort(this.comparator);
        this.setState({ data: key });
      })
      .catch((error) => console.error(error))
      .finally(() => {
        this.setState({ isLoading: false });
      });
  };

  componentDidMount() {
    this.fetchURL();
  }

  render() {
    const { data, isLoading } = this.state;
    const { navigate } = this.props.navigation;
    //console.log(window.id);
    return (
    <View style={{ backgroundColor: "#f9c14a", height: "100%" }}>
                <View style={{
                    flexDirection: "row", 
                    marginTop: 35,
                    paddingVertical: 2,
                    
                }}>
                   
                   <Icon style={{
                           marginLeft: 10,
                           alignSelf:"flex-end"
                       }} name="logout" color="#f9c14a" size={24}/>
                    
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
                        marginRight: "auto",
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
                  
                <Icon 
                onPress={() => {
                    fetch(`http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/links?userID=${window.id}&url=${this.state.longURL}`,{ method: 'POST', headers: { Accept: 'application/json', 'Content-Type': 'application/json' }})
                    .then((response) => response.json())
                    .then((json) => {
                        this.setState({ data: json });
                        if(json.status == 200){//console.log(json.urlKey); 
                            this.fetchURL();}
                        else{
                            Alert.alert(
                                "Problem Occured!",
                                json.message,
                                [
                                    {
                                        text: "OK"
                                    }
                                ]
                            )
                            this.fetchURL();
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
                    name="checkcircle" color="#ed5c61" size={30} />     
            </View>

                <Text  onPress={() => navigate('Custom')}
                    style={{
                        marginTop: 15,
                        color: "#000",
                        fontFamily: "SemiBold",
                        paddingHorizontal: 10,
                        marginLeft:5,
                        fontSize: 15,
                        textDecorationLine: 'underline'
                }}>Make Custom URLs Here!</Text>

                <Text
                    style={{
                        marginTop: 20,
                        color: "#eb4067",
                        fontFamily: "SemiBold",
                        paddingHorizontal: 10,
                        marginLeft:5,
                        fontSize: 18
                    }}>Converted URLs:</Text>


    <View style={{ flex: 1, marginTop: 5, marginBottom: 20}}>
      <SafeAreaView>
        {isLoading ? <ActivityIndicator/> : (
          <FlatList
            data={data}
            keyExtractor={({ key }, index) => key}
            renderItem={({ item }) => (
              <Text onPress={() => {

                this.state.copy = "http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/" + item.key;
                Clipboard.setString(this.state.copy);
                ToastAndroid.showWithGravity("Link Copied", ToastAndroid.SHORT, ToastAndroid.BOTTOM);
                Alert.alert(
                    "Open Link?",
                    "Link copied to clipboard. Do you want to open the link?",
                    [
                        {
                            text: "No",
                            style: "cancel"
                        },
                        { text: "Yes", onPress: () => {
                            var new_url = "http://cs443-elb-1872842034.eu-central-1.elb.amazonaws.com:5000/api/" + item.key;
                            Linking.canOpenURL(new_url).then(supported => {
                            if(supported){Linking.openURL(new_url);}
                            else{console.log("cannot open link");}
                        })
                    } }
                    ]
                )
                }}
                style={{
                marginTop: 15,
                color: "#000",
                fontFamily: "SemiBold",
                paddingHorizontal: 10,
                marginLeft:5,
                fontSize: 15
            }}>{item.key}</Text>
            )}
          />
        )}
      </SafeAreaView>
      </View>
      </View>
        );
    }
}

const styles = StyleSheet.create({
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
        marginTop: 20,
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