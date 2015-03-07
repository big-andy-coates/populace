# Populace
Populace is a library to help you populate object graphs. It's main use is around testing of serialisation/de-serialisation
mechanisms where, to ensure good test coverage, you require complex domain object graphs to be full populated.

Populace supports integration points to allow the behaviour of how the object graph is walked, how new entities are
instantiated, and how existing fields are mutated. This allows Populace to be fully customised to match the domain model
and the needs of your project.

# License

Populace is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.

# How to build
## Prerequisites
Before building the code you'll need:

* JDK 1.8 or above
* To clone this repo.

## Howto: Compile and run the tests.
Run the following commmand from the root directory of the project:
> `gradlew`

The script automatically downloads the correct Gradle version, so you won't have to do anything else. Gradle will then
compile the project, run the tests, generate the java docs and build the project jars.
If everything runs successfully, each module's jar will be located under its build/libs directory.

To see a full list of tasks available run:
> `gradlew tasks`

## Howto: Create IntelliJ project files
Gradle includes a task that creates all files required to develop populace in IntelliJ. Run the following command:
> `gradlew idea`

Then import the project into your workspace or open the root populace.ipr project file.

## Howto: Create Eclipse project files
Gradle includes a task that creates all files required to develop populace in Eclipse. Run the following command:
> `gradlew ecplise`

Then import the project into your workspace.

# Contributing
Contributing is good! Please get stuck in, but first read [notes on contributing](./CONTRIBUTING.md)


