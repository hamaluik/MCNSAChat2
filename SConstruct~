# create the environment with the classpath set to include libraries
env = Environment(ENV = {'CLASSPATH': 'lib/bukkit.jar:lib/PermissionsEx.jar'})

# build the classes
env.Java('classes', 'src')

# assemble the jar
env.Jar(target = 'bin/MCNSAChat2.jar', source = ['classes', 'plugin.yml'])
