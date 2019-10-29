const Bot = require('keybase-bot')

const bot = new Bot()
const username = 'enna'
const paperkey = 'manual lyrics dog jacket initial success ethics about grant radio joy duck metal'
bot
    .init(username, paperkey, {verbose: false})
    .then(() => {
        console.log(`Your bot is initialized. It is logged in as ${bot.myInfo().username}`)

        const channel ={name: 'mkbot', public: false, topicType: 'chat', membersType: 'team', topicName: 'test1'}
        const message = {
            body: `Hello mkbot!!!! This is ${bot.myInfo().username}, saying hello to you all once again!!!`,
        }

        bot.chat
            .send(channel, message)
            .then(() => {
                console.log('Message sent!')
                bot.deinit()
            })
            .catch(error => {
                console.error(error)
                bot.deinit()
            })
    })
    .catch(error => {
        console.error(error)
        bot.deinit()
    })

