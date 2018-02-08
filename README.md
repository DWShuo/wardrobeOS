
# wardrobeOS

virtual wardrobe application

## Getting Started

git clone https://github.com/DWShuo/wardrobeOS.git

### Prerequisites

Andrioid phone

### Installing

git clone https://github.com/DWShuo/wardrobeOS.git

### Coding Style 
We are using Google java style guide, the following is a condensed version of Google's style guide. [More Info](https://google.github.io/styleguide/javaguide.html)
* Variable declarations  
  * Class names are written in UpperCamelCase. 
  * Variable names are written in lowerCamelCase.  
  * Method names are written in lowerCamelCase. 
  * Constant names use CONSTANT_CASE: all uppercase letters, with each word separated from the next by a single underscore  
* One variable per declaration  
  * Every variable declaration (field or local) declares only one variable: declarations such as int a, b; are not used.
* Braces follow the Kernighan and Ritchie style ("Egyptian brackets") for nonempty blocks and block-like constructs:  
    * No line break before the opening brace.  
    * Line break after the opening brace.  
    * Line break before the closing brace.  
    * Line break after the closing brace, only if that brace terminates a statement or terminates the body of a method, constructor, or named class. For example, there is no line break after the brace if it is followed by else or a comma.  
```
return () -> {
  while (condition()) {
    method();
  }
};

return new MyClass() {
  @Override public void method() {
    if (condition()) {
      try {
        something();
      } catch (ProblemException e) {
        recover();
      }
    } else if (otherCondition()) {
      somethingElse();
    } else {
      lastThing();
    }
  }
};
```
* Empty blocks should be concise  
```
// This is acceptable
  void doNothing() {}`
  
// This is not acceptable: No concise empty blocks in a multi-block statement
  try {
    doSomething();
  } catch (Exception e) {}
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing
David Wang  
Meghnath Shankar  
Enoch Huang  
