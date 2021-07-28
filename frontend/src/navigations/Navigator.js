import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import Login from '../screens/Login';
import Register from '../screens/Register'
import Custom from '../screens/Custom'
import Main from '../screens/Main'


const stackNavigatorOptions = {
    headerShown: false
}
const AppNavigator = createStackNavigator({
    Login: { screen: Login },
    Register: { screen: Register },
    Custom: { screen: Custom },
    Main: { screen: Main },
},
    {
        defaultNavigationOptions: stackNavigatorOptions
    }
);
export default createAppContainer(AppNavigator);